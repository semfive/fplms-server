using DiscussionService.Contracts;
using DiscussionService.Data;
using DiscussionService.Models;

namespace DiscussionService.Repositories
{
    public class QuestionRepository : RepositoryBase<Question>, IQuestionRepository
    {
        public QuestionRepository(RepositoryContext repositoryContext) : base(repositoryContext)
        {

        }
    }
}