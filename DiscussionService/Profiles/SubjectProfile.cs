using AutoMapper;
using DiscussionService.Dtos;
using DiscussionService.Models;
namespace DiscussionService.Profiles
{
    public class SubjectProfile : Profile
    {
        public SubjectProfile()
        {
            CreateMap<CreateSubjectDto, Subject>();
            CreateMap<Subject, GetSubjectDto>();
        }
    }
}