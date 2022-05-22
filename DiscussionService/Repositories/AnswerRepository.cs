using DiscussionService.Contracts;
using DiscussionService.Data;
using DiscussionService.Models;

namespace DiscussionService.Repositories
{
    public class AnswerRepository : RepositoryBase<Answer>, IAnswerRepository
    {
        public AnswerRepository(RepositoryContext repositoryContext) : base(repositoryContext)
        {

        }
    }
}